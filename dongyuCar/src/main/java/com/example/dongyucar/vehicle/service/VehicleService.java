package com.example.dongyucar.vehicle.service;

import com.example.dongyucar.review.service.S3Service;
import com.example.dongyucar.vehicle.domain.entity.Vehicle;
import com.example.dongyucar.vehicle.domain.entity.VehicleDetail;
import com.example.dongyucar.vehicle.domain.entity.VehicleImage;
import com.example.dongyucar.vehicle.domain.entity.VehicleOption;
import com.example.dongyucar.vehicle.domain.repository.VehicleDetailRepository;
import com.example.dongyucar.vehicle.domain.repository.VehicleImageRepository;
import com.example.dongyucar.vehicle.domain.repository.VehicleOptionRepository;
import com.example.dongyucar.vehicle.domain.repository.VehicleRepository;
import com.example.dongyucar.vehicle.dto.request.VehicleRequestDto;
import com.example.dongyucar.vehicle.dto.response.VehicleDetailResponseDto;
import com.example.dongyucar.vehicle.dto.response.VehicleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleDetailRepository detailRepository;
    private final VehicleImageRepository imageRepository;
    private final VehicleOptionRepository optionRepository;
    private final S3Service s3Service;

    // 차량 등록
    public Long createVehicle(VehicleRequestDto dto) throws Exception {

        Vehicle vehicle = Vehicle.builder()
                .title(dto.getTitle())
                .model(dto.getModel())
                .year(dto.getYear())
                .mileage(dto.getMileage())
                .price(dto.getPrice())
                .monthFee(dto.getMonthFee())
                .supportFee(dto.getSupportFee())
                .build();

        vehicleRepository.save(vehicle);

        VehicleDetail detail = VehicleDetail.builder()
                .color(dto.getColor())
                .fuelType(dto.getFuelType())
                .gearType(dto.getGearType())
                .accidentHistory(dto.getAccidentHistory())
                .vehicle(vehicle)
                .build();

        detailRepository.save(detail);


        // 🚀🚀 이미지 업로드는 절대 기다리지 않고 비동기로 처리
        validateVehicle(dto, vehicle);

        // 옵션 저장
        if (dto.getOptions() != null) {
            dto.getOptions().forEach(opt ->
                    optionRepository.save(
                            VehicleOption.builder()
                                    .vehicle(vehicle)
                                    .name(opt)
                                    .checked(true)
                                    .build()
                    )
            );
        }

        // 🚀 **이미지 기다리지 않고 바로 응답**
        return vehicle.getId();
    }

    private void validateVehicle(final VehicleRequestDto dto, final Vehicle vehicle) {
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {

            CompletableFuture.runAsync(() -> {

                dto.getImages().forEach(file -> {
                    try {
                        String url = s3Service.uploadFile(
                                file.getInputStream(),
                                file.getOriginalFilename(),
                                file.getSize(),
                                file.getContentType()
                        );

                        imageRepository.save(
                                VehicleImage.builder()
                                        .vehicle(vehicle)
                                        .imageUrl(url)
                                        .build()
                        );

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

            });
        }
    }

    // 목록 조회
    public Page<VehicleResponseDto> getVehiclePage(int page) {

        Pageable pageable = PageRequest.of(page, 6);

        Page<Vehicle> vehicles = vehicleRepository.findPageWithDetail(pageable);

        return vehicles.map(v -> {

            // 썸네일 찾기
            String thumbnail = imageRepository.findByVehicleId(v.getId())
                    .stream()
                    .findFirst()
                    .map(VehicleImage::getImageUrl)
                    .orElse(null);

            VehicleDetail detail = detailRepository.findByVehicleId(v.getId()).orElse(null);

            return VehicleResponseDto.builder()
                    .id(v.getId())
                    .title(v.getTitle())
                    .thumbnail(thumbnail)

                    // 🔥 리스트에서 같이 내려줄 값들
                    .year(v.getYear())
                    .price(v.getPrice())
                    .mileage(v.getMileage())
                    .fuelType(detail != null ? detail.getFuelType() : null)
                    .gearType(detail != null ? detail.getGearType() : null)
                    .color(detail != null ? detail.getColor() : null)

                    .build();
        });
    }

    // 상세 조회
    public VehicleDetailResponseDto getVehicle(Long id) {

        Vehicle vehicle = vehicleRepository.findDetail(id).orElseThrow();
        VehicleDetail detail = detailRepository.findByVehicleId(id).orElseThrow();
        List<VehicleImage> images = imageRepository.findByVehicleId(id);
        List<VehicleOption> options = optionRepository.findByVehicleId(id);

        return VehicleDetailResponseDto.builder()
                .id(vehicle.getId())
                .title(vehicle.getTitle())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .mileage(vehicle.getMileage())
                .price(vehicle.getPrice())
                .monthFee(vehicle.getMonthFee())
                .supportFee(vehicle.getSupportFee())
                .color(detail.getColor())
                .fuelType(detail.getFuelType())
                .gearType(detail.getGearType())
                .accidentHistory(detail.getAccidentHistory())
                .images(images.stream().map(VehicleImage::getImageUrl).toList())
                .options(options.stream().map(VehicleOption::getName).toList())
                .build();
    }

    // 차량 삭제
    public void deleteVehicle(Long id) {

        // 이미지 S3 삭제 포함
        List<VehicleImage> images = imageRepository.findByVehicleId(id);
        for (VehicleImage img : images) {
            s3Service.deleteFile(img.getImageUrl());
        }

        imageRepository.deleteAll(images);
        optionRepository.deleteAll(optionRepository.findByVehicleId(id));
        detailRepository.delete(detailRepository.findByVehicleId(id).orElseThrow());
        vehicleRepository.deleteById(id);
    }

    // 차량 수정 (이미지는 추가만)
    public VehicleDetailResponseDto updateVehicle(Long id, VehicleRequestDto dto) throws Exception {

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        vehicle.setTitle(dto.getTitle());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());
        vehicle.setPrice(dto.getPrice());
        vehicle.setMonthFee(dto.getMonthFee());
        vehicle.setSupportFee(dto.getSupportFee());
        vehicleRepository.save(vehicle);

        VehicleDetail detail = detailRepository.findByVehicleId(id).orElseThrow();
        detail.setColor(dto.getColor());
        detail.setFuelType(dto.getFuelType());
        detail.setGearType(dto.getGearType());
        detail.setAccidentHistory(dto.getAccidentHistory());
        detailRepository.save(detail);

        // 새 이미지 추가 (빈 파일 필터링 추가)
        if (dto.getImages() != null) {

            List<MultipartFile> validImages = dto.getImages().stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .toList();

            for (MultipartFile file : validImages) {

                String url = s3Service.uploadFile(
                        file.getInputStream(),
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType()
                );

                imageRepository.save(
                        VehicleImage.builder()
                                .vehicle(vehicle)
                                .imageUrl(url)
                                .build()
                );
            }
        }

        // 옵션 전체 교체
        List<VehicleOption> oldOpts = optionRepository.findByVehicleId(id);
        optionRepository.deleteAll(oldOpts);

        if (dto.getOptions() != null) {
            dto.getOptions().forEach(name ->
                    optionRepository.save(
                            VehicleOption.builder()
                                    .vehicle(vehicle)
                                    .name(name)
                                    .checked(true)
                                    .build()
                    )
            );
        }

        return getVehicle(id);
    }

    // 이미지 개별 삭제
    public void deleteImage(Long vehicleId, Long imageId) {

        VehicleImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));

        if (!image.getVehicle().getId().equals(vehicleId)) {
            throw new RuntimeException("해당 차량의 이미지가 아닙니다.");
        }

        s3Service.deleteFile(image.getImageUrl());
        imageRepository.delete(image);
    }

    // 검색
    public Page<VehicleResponseDto> searchVehicles(String keyword, int page) {

        Pageable pageable = PageRequest.of(page, 6);

        Page<Vehicle> vehicles = vehicleRepository.searchByModel(keyword, pageable);

        return vehicles.map(v -> {
            String thumbnail = imageRepository.findByVehicleId(v.getId())
                    .stream()
                    .findFirst()
                    .map(VehicleImage::getImageUrl)
                    .orElse(null);

            return VehicleResponseDto.builder()
                    .id(v.getId())
                    .title(v.getTitle())
                    .thumbnail(thumbnail)
                    .build();
        });
    }
}
