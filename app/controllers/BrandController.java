package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.mapper.BrandMapper;
import dto.BrandToPostDto;
import dto.BrandToPutDto;
import persistence.model.Brand;
import persistence.repository.BrandRepository;
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

    public CompletionStage<Result> getBrand(long id) {
        return brandRepository
            .find(id)
            .thenApplyAsync(
                brand -> {
                    if (brand != null) {
                        return ok(Json.toJson(BrandMapper.makeBrandDto(brand)));
                    }
                    return notFound(String.format("Brand with id %d cannot be found.", id));
                });
    }

    public CompletionStage<Result> getBrands() {
        return brandRepository
            .list()
            .thenApplyAsync(
                brands -> ok(Json.toJson(brands
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
                .create(brand)
                .thenApplyAsync(
                    brandCreated -> created(Json.toJson(BrandMapper.makeBrandDto(brandCreated)))
                        .withHeader(
                            LOCATION,
                            routes.BrandController.getBrand(brandCreated.getId()).toString()));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateBrand(long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return supplyAsync(() -> badRequest("Expecting Json data"));
        } else {
            BrandToPutDto brandDto = Json.fromJson(json, BrandToPutDto.class); // TODO: runtime exception
            if (id != brandDto.getId()) {
                return supplyAsync(() -> badRequest(
                    String.format("Brand id %d does not match the id in the route: %d.", brandDto.getId(), id)));
            }
            if (brandDto.getId() == 0) {
                return supplyAsync(() -> badRequest(
                    String.format("Brand id cannot be 0.")));
            }
            Brand brand = BrandMapper.makeBrand(brandDto);
            return brandRepository
                .update(brand)
                .thenApplyAsync(
                    updated -> {
                        if (!updated) {
                            return badRequest(String.format("Brand with id %d cannot be found.", id));
                        }
                        return noContent();
                    });

        }
    }

    public CompletionStage<Result> deleteBrand(long id) {
        return brandRepository
            .delete(id)
            .thenApplyAsync(
                removed -> {
                    if (!removed) {
                        return notFound(String.format("Brand with id %d cannot be found.", id));
                    }
                    return noContent();
                });
    }
}
