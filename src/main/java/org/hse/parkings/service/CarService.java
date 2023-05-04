package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Car;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.carCache;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final EmployeeService employeeService;

    public Car save(Car car) throws NotFoundException {
        Car toSave = Car.builder()
                .ownerId(car.getOwnerId())
                .model(car.getModel())
                .lengthMeters(car.getLengthMeters())
                .weightTons(car.getWeightTons())
                .registryNumber(car.getRegistryNumber()).build();
        employeeService.find(toSave.getOwnerId());
        carRepository.save(toSave);
        carCache.remove(toSave.getId());
        return findCar(toSave.getId());
    }

    public Car update(Car car) throws NotFoundException {
        employeeService.find(car.getOwnerId());
        carRepository.update(car);
        carCache.remove(car.getId());
        return findCar(car.getId());
    }

    public void delete(UUID id) {
        carRepository.delete(id);
        carCache.remove(id);
    }

    public void deleteAll() {
        carRepository.deleteAll();
        carCache.clear();
    }

    public Car findCar(UUID id) throws NotFoundException {
        if (carCache.containsKey(id)) {
            return carCache.get(id);
        }
        Car car = carRepository.find(id)
                .orElseThrow(() -> new NotFoundException("Car with id = " + id + " not found"));
        carCache.put(id, car);
        return car;
    }

    public Set<Car> findAll() {
        return carRepository.findAll();
    }

    public Set<Car> findEmployeesCars(UUID employeeId) {
        return carRepository.findEmployeesCars(employeeId);
    }
}
