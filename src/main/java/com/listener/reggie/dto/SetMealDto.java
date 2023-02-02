package com.listener.reggie.dto;

import com.listener.reggie.entity.SetMeal;
import com.listener.reggie.entity.SetMealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setmealDishes;

    private String categoryName;
}
