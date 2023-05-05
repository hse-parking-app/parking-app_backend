package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.carCache;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final EmployeeService employeeService;

    private final AuthService authService;

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

    public Car saveEmployeeCar(Car car) {
        JwtAuthentication authInfo = authService.getAuthInfo();
        car.setOwnerId(authInfo.getId());
        return save(car);
    }

    public Car update(Car car) {
        carRepository.update(car);
        carCache.remove(car.getId());
        return findCar(car.getId());
    }

    public Car updateEmployeeCar(Car car) {
        Car employeeCar = findCar(car.getId());
        JwtAuthentication authInfo = authService.getAuthInfo();

        if (employeeCar.getOwnerId() != authInfo.getId()) {
            throw new NotFoundException("Car with id = " + car.getId() + " not found");
        }
        car.setOwnerId(authInfo.getId());

        return update(car);
    }

    public void delete(UUID id) {
        carRepository.delete(id);
        carCache.remove(id);
    }

    public void deleteAll() {
        carRepository.deleteAll();
        carCache.clear();
    }

    public void deleteEmployeeCar(UUID id) throws NotFoundException {
        Car car = findCar(id);
        JwtAuthentication authInfo = authService.getAuthInfo();

        if (car.getOwnerId() != authInfo.getId()) {
            throw new NotFoundException("Car with id = " + id + " not found");
        }

        delete(id);
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
