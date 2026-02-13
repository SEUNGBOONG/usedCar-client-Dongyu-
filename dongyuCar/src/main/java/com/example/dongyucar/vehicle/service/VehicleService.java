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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleDetailRepository detailRepository;
    private final VehicleImageRepository imageRepository;
    private final VehicleOptionRepository optionRepository;
    private final S3Service s3Service;

    // 1. 차량 등록
    public Long createVehicle(VehicleRequestDto dto) throws Exception {
        Vehicle vehicle = Vehicle.builder()
                .title(dto.getTitle())
                .model(dto.getModel())
                .year(dto.getYear())
                .mileage(dto.getMileage())
                .price(dto.getPrice())
                .monthFee(dto.getMonthFee())
                .supportFee(dto.getSupportFee())
                .description(dto.getDescription()) // 🔥 [추가됨] 설명 저장
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

        if (dto.getOptions() != null) {
            dto.getOptions().forEach(opt ->
                    optionRepository.save(VehicleOption.builder().vehicle(vehicle).name(opt).checked(true).build())
            );
        }

        uploadVehicleImages(dto.getImages(), vehicle);

        return vehicle.getId();
    }

    // 2. 차량 수정
    public VehicleDetailResponseDto updateVehicle(Long id, VehicleRequestDto dto) throws Exception {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        // 기본 정보 업데이트
        vehicle.setTitle(dto.getTitle());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());
        vehicle.setPrice(dto.getPrice());
        vehicle.setMonthFee(dto.getMonthFee());
        vehicle.setSupportFee(dto.getSupportFee());
        vehicle.setDescription(dto.getDescription()); // 🔥 [추가됨] 설명 수정

        vehicleRepository.save(vehicle);

        // 상세 정보 업데이트
        VehicleDetail detail = detailRepository.findByVehicleId(id).orElseThrow();
        detail.setColor(dto.getColor());
        detail.setFuelType(dto.getFuelType());
        detail.setGearType(dto.getGearType());
        detail.setAccidentHistory(dto.getAccidentHistory());

        // 이미지 추가
        uploadVehicleImages(dto.getImages(), vehicle);

        // 옵션 전체 교체
        List<VehicleOption> oldOpts = optionRepository.findByVehicleId(id);
        optionRepository.deleteAll(oldOpts);

        if (dto.getOptions() != null) {
            dto.getOptions().forEach(name ->
                    optionRepository.save(VehicleOption.builder().vehicle(vehicle).name(name).checked(true).build())
            );
        }

        return getVehicle(id);
    }

    // 3. 목록 조회 (목록에는 보통 설명이 안 나가므로 그대로 둠)
    public Page<VehicleResponseDto> getVehiclePage(int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Vehicle> vehicles = vehicleRepository.findPageWithDetail(pageable);

        return vehicles.map(v -> {
            String thumbnail = imageRepository.findByVehicleId(v.getId())
                    .stream().findFirst().map(VehicleImage::getImageUrl).orElse(null);
            VehicleDetail detail = detailRepository.findByVehicleId(v.getId()).orElse(null);

            return VehicleResponseDto.builder()
                    .id(v.getId())
                    .title(v.getTitle())
                    .thumbnail(thumbnail)
                    .year(v.getYear())
                    .price(v.getPrice())
                    .mileage(v.getMileage())
                    .fuelType(detail != null ? detail.getFuelType() : null)
                    .gearType(detail != null ? detail.getGearType() : null)
                    .color(detail != null ? detail.getColor() : null)
                    .build();
        });
    }

    // 4. 상세 조회 (설명 필드 추가)
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
                .description(vehicle.getDescription()) // 🔥 [추가됨] 상세 조회 시 설명 반환
                .color(detail.getColor())
                .fuelType(detail.getFuelType())
                .gearType(detail.getGearType())
                .accidentHistory(detail.getAccidentHistory())
                .images(images.stream().map(VehicleImage::getImageUrl).toList())
                .options(options.stream().map(VehicleOption::getName).toList())
                .build();
    }

    // (나머지 삭제, 검색, 이미지 메서드는 기존과 동일하므로 생략하지 않고 아래 붙임)
    private void uploadVehicleImages(List<MultipartFile> files, Vehicle vehicle) {
        if (files == null || files.isEmpty()) return;
        files.stream().filter(f -> f != null && !f.isEmpty()).forEach(file -> {
            try {
                String url = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getSize(), file.getContentType());
                imageRepository.save(VehicleImage.builder().vehicle(vehicle).imageUrl(url).build());
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        });
    }

    public void deleteVehicle(Long id) {
        List<VehicleImage> images = imageRepository.findByVehicleId(id);
        images.forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        imageRepository.deleteAll(images);
        optionRepository.deleteAll(optionRepository.findByVehicleId(id));
        detailRepository.delete(detailRepository.findByVehicleId(id).orElseThrow());
        vehicleRepository.deleteById(id);
    }

    public void deleteImage(Long vehicleId, Long imageId) {
        VehicleImage image = imageRepository.findById(imageId).orElseThrow();
        if (!image.getVehicle().getId().equals(vehicleId)) throw new RuntimeException("권한 없음");
        s3Service.deleteFile(image.getImageUrl());
        imageRepository.delete(image);
    }

    public Page<VehicleResponseDto> searchVehicles(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return vehicleRepository.searchByModel(keyword, pageable).map(v -> {
            String thumbnail = imageRepository.findByVehicleId(v.getId()).stream().findFirst().map(VehicleImage::getImageUrl).orElse(null);
            return VehicleResponseDto.builder().id(v.getId()).title(v.getTitle()).thumbnail(thumbnail).build();
        });
    }
}
