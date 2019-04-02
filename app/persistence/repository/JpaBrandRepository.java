package persistence.repository;

import persistence.model.Brand;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JpaBrandRepository implements BrandRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JpaBrandRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Brand> find(long id) {
        return supplyAsync(() -> wrap(em -> getBrand(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Brand>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Brand> add(Brand person) {
        return supplyAsync(() -> wrap(em -> insert(em, person)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Brand insert(EntityManager em, Brand brand) {
        em.persist(brand);
        return brand;
    }

    private Stream<Brand> list(EntityManager em) {
        List<Brand> brands = em.createQuery("select b from Brand b", Brand.class).getResultList();
        return brands.stream();
    }

    private Brand getBrand(EntityManager em, long id) {
        Query query = em.createQuery("select b from Brand b where b.id=?1", Brand.class);
        query.setParameter(1, id);
        Brand brand = null;
        try {
            brand = (Brand) query.getSingleResult();
        } catch (NoResultException ex) {
            // TODO: log no result?
        }
        return brand;
    }
}
