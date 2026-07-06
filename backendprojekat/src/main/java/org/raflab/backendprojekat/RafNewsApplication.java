package org.raflab.backendprojekat;

import com.fasterxml.jackson.core.util.JacksonFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.raflab.backendprojekat.filters.CorsFilter;
import org.raflab.backendprojekat.repository.category.CategoryRepository;
import org.raflab.backendprojekat.repository.category.MySqlCategoryRepository;
import org.raflab.backendprojekat.repository.comment.CommentRepository;
import org.raflab.backendprojekat.repository.comment.MySqlCommentRepository;
import org.raflab.backendprojekat.repository.news.MySqlNewsRepository;
import org.raflab.backendprojekat.repository.news.NewsRepository;
import org.raflab.backendprojekat.repository.reaction.MySqlReactionRepository;
import org.raflab.backendprojekat.repository.reaction.ReactionRepository;
import org.raflab.backendprojekat.repository.tag.MySqlTagRepository;
import org.raflab.backendprojekat.repository.tag.TagRepository;
import org.raflab.backendprojekat.repository.user.MySqlUserRepository;
import org.raflab.backendprojekat.repository.user.UserRepository;
import org.raflab.backendprojekat.service.*;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@ApplicationPath("/api")
public class RafNewsApplication extends ResourceConfig {

    public RafNewsApplication() {
        // Ukljucujemo validaciju - greske validacije se salju u response
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        // Registrujemo Jackson sa JSR310 modulom za LocalDateTime
        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
        register(CorsFilter.class);

        // Registrujemo DI bindings
        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(MySqlUserRepository.class).to(UserRepository.class).in(Singleton.class);
                this.bind(MySqlCategoryRepository.class).to(CategoryRepository.class).in(Singleton.class);
                this.bind(MySqlNewsRepository.class).to(NewsRepository.class).in(Singleton.class);
                this.bind(MySqlCommentRepository.class).to(CommentRepository.class).in(Singleton.class);
                this.bind(MySqlReactionRepository.class).to(ReactionRepository.class).in(Singleton.class);
                this.bind(MySqlTagRepository.class).to(TagRepository.class).in(Singleton.class);

                this.bindAsContract(UserService.class).in(Singleton.class);
                this.bindAsContract(CategoryService.class).in(Singleton.class);
                this.bindAsContract(TagService.class).in(Singleton.class);
                this.bindAsContract(NewsService.class).in(Singleton.class);
                this.bindAsContract(CommentService.class).in(Singleton.class);
                this.bindAsContract(ReactionService.class).in(Singleton.class);
            }
        };
        register(binder);

        packages("org.raflab.backendprojekat");
    }
    @Provider
    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;

        public ObjectMapperProvider() {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
}

