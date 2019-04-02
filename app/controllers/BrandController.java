package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.mapper.BrandMapper;
import dto.BrandDto;
import dto.BrandToPostDto;
import persistence.model.Brand;
import persistence.repository.BrandRepository;
import play.core.j.HttpExecutionContext;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BrandController extends Controller {
    private final BrandRepository brandRepository;

    @Inject
    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Result getBrand(long id) {
        return badRequest(String.format("Brand with id %d cannot be found.", id));
    }

    public CompletionStage<Result> getBrands() {
        return brandRepository
            .list()
            .thenApplyAsync(brands -> ok(
                Json.toJson(brands
                    .map(BrandMapper::makeBrandDto)
                    .collect(Collectors.toList()))));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createBrand(Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return supplyAsync(() -> badRequest("Expecting Json data"));
        } else {
            BrandToPostDto brandDto = Json.fromJson(json, BrandToPostDto.class); // TODO: runtime exception
            Brand brand = BrandMapper.makeBrand(brandDto);
            return brandRepository
                .add(brand)
                .thenApplyAsync(brandCreated ->
                    created(Json.toJson(BrandMapper.makeBrandDto(brandCreated)))
                        .withHeader(
                            LOCATION,
                            routes.BrandController.getBrand(brandCreated.getId()).toString()));
        }
    }
}
