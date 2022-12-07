package org.example.coursework.service;

import java.util.Set;
import java.util.UUID;

import org.example.coursework.dao.CarRepository;
import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.Car;
import org.springframework.stereotype.Service;

import static org.example.coursework.utils.Cache.carCache;

@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public Car save(Car car) {
        repository.save(car);
        carCache.remove(car.getId());
        return findCar(car.getId());
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
