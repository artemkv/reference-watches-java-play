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
    public CompletionStage<Brand> create(Brand person) {
        return supplyAsync(() -> wrap(em -> insert(em, person)), executionContext);
    }

    @Override
    public CompletionStage<Boolean> update(Brand person) {
        return supplyAsync(() -> wrap(em -> update(em, person)), executionContext);
    }

    @Override
    public CompletionStage<Boolean> delete(long id) {
        return supplyAsync(() -> wrap(em -> delete(em, id)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Brand getBrand(EntityManager em, long id) {
        return em.find(Brand.class, id);
    }

    private Stream<Brand> list(EntityManager em) {
        List<Brand> brands = em.createQuery("select b from Brand b", Brand.class).getResultList();
        return brands.stream();
    }

    private Brand insert(EntityManager em, Brand brand) {
        em.persist(brand);
        return brand;
    }

    private boolean update(EntityManager em, Brand brand) {
        Brand existing = em.find(Brand.class, brand.getId());
        if (existing == null) {
            return false;
        }
        em.merge(brand);
        return true;
    }

    private boolean delete(EntityManager em, long id) {
        Brand existing = em.find(Brand.class, id);
        if (existing == null) {
            return false;
        }
        em.remove(existing);
        return true;
    }
}
