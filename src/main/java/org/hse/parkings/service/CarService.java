package org.hse.parkings.service;

import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Car;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.carCache;

@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public Car save(Car car) throws AlreadyExistsException {
        Car toSave = Car.builder()
                .id((car.getId() == null) ? UUID.randomUUID() : car.getId())
                .model(car.getModel())
                .lengthMeters(car.getLengthMeters())
                .weightTons(car.getWeightTons())
                .registryNumber(car.getRegistryNumber()).build();
        repository.find(toSave.getId()).ifPresent(s -> {
            throw new AlreadyExistsException("Car with id = " + s.getId() + " already exists");
        });
        repository.save(toSave);
        carCache.remove(toSave.getId());
        return findCar(toSave.getId());
    }

    public Car update(Car car) {
        repository.update(car);
        carCache.remove(car.getId());
        return findCar(car.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        carCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        carCache.clear();
    }

    public Car findCar(UUID id) throws NotFoundException {
        if (carCache.containsKey(id)) {
            return carCache.get(id);
        } else {
            Car car = repository
                    .find(id)
                    .orElseThrow(() -> new NotFoundException("Car with id = " + id + " not found"));
            carCache.put(id, car);
            return car;
        }
    }

    public Set<Car> findAll() {
        return repository.findAll();
    }
}
