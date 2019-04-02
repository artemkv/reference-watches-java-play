package controllers;

import dto.BrandDto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public class BrandController extends Controller {
    public Result getBrand(long id) {
        return badRequest(String.format("Brand with id %d cannot be found.", id));
    }

    public Result getBrands() {
        List<BrandDto> brands = Arrays.asList(
            getBrandRolex(),
            getBrandPiguet()
        );

        return ok(Json.toJson(brands));
    }

    public static BrandDto getBrandRolex() {
        BrandDto brand = new BrandDto();
        brand.setId(1);
        brand.setTitle("Rolex");
        brand.setYearFounded(1915);
        brand.setDescription("Swiss luxury watch manufacturer");
        return brand;
    }

    public static BrandDto getBrandPiguet() {
        BrandDto brand = new BrandDto();
        brand.setId(2);
        brand.setTitle("Audemars Piguet");
        brand.setYearFounded(1875);
        brand.setDescription("Swiss manufacturer of luxury mechanical watches and clocks");
        return brand;
    }
}
