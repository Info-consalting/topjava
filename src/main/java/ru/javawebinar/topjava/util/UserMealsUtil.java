package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );

        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println("-------------------------------------------------------------------------------------------------");

        getFilteredWithExceededOnPass(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println("-------------------------------------------------------------------------------------------------");

          getFilteredWithExceededOnPass2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

    }


     static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> map = mealList.stream()
                .collect(Collectors.groupingBy(UserMeal::getLocalDate, Collectors.summingInt(UserMeal::getCalories)));

        return mealList.stream()
                .filter(m->TimeUtil.isBetween(m.getLocalTime(), startTime, endTime))
                .map(m -> createUserMealWithExceed(m, map.get(m.getLocalDate())>caloriesPerDay)).collect(Collectors.toList());
    }

    static List<UserMealWithExceed> getFilteredWithExceededOnPass(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map = new HashMap<>();
       List<UserMealWithExceed> list = new ArrayList<>();

        mealList.forEach(m->map.merge(m.getLocalDate(), m.getCalories(), Integer::sum));

        mealList.stream()
                .filter(m->TimeUtil.isBetween(m.getLocalTime(), startTime, endTime))
               .forEach(m->list.add(createUserMealWithExceed(m, map.get(m.getLocalDate())>caloriesPerDay)));

        return list;
    }

    static List<UserMealWithExceed> getFilteredWithExceededOnPass2(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Collection<List<UserMeal>> values = mealList.stream()
                .collect(Collectors.groupingBy(UserMeal::getLocalDate)).values();

     return values.stream()
             .flatMap(s->{
                 boolean exceed = s.stream().mapToInt(UserMeal::getCalories).sum()>caloriesPerDay;
                 return   s.stream().
                         filter(m->TimeUtil.isBetween(m.getLocalTime(),startTime,endTime))
                         .map(m->createUserMealWithExceed(m, exceed));
                }).collect(Collectors.toList());
    }

        private static UserMealWithExceed createUserMealWithExceed(UserMeal userMeal, boolean exceed){
        return new UserMealWithExceed(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), exceed);
    }
}
