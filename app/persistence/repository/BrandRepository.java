package persistence.repository;

import com.google.inject.ImplementedBy;
import persistence.model.Brand;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JpaBrandRepository.class)
public interface BrandRepository {
    CompletionStage<Brand> find(long id);
    CompletionStage<Stream<Brand>> list();
    CompletionStage<Brand> create(Brand person);
    CompletionStage<Boolean> update(Brand person);
    CompletionStage<Boolean> delete(long id);
}
