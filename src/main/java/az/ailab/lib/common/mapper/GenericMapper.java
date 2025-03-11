package az.ailab.lib.common.mapper;

import java.util.List;
import org.mapstruct.MappingTarget;

/**
 * Generic mapper interface that provides common mapping operations between entities and DTOs.
 * <p>
 * This interface defines standard conversion methods for mapping between domain entities and 
 * data transfer objects (both request and response DTOs). Implement this interface with MapStruct
 * to generate the mapping code automatically.
 * </p>
 *
 * @param <E> Entity type - the domain model class
 * @param <R> Request DTO type - used for incoming data/requests
 * @param <S> Response DTO type - used for outgoing data/responses
 */
public interface GenericMapper<E, R, S> {

    /**
     * Maps an entity to a request DTO.
     *
     * @param entity the entity to convert
     * @return the resulting request DTO
     */
    R mapToRequestDto(E entity);

    /**
     * Maps an entity to a response DTO.
     *
     * @param entity the entity to convert
     * @return the resulting response DTO
     */
    S mapToResponseDto(E entity);

    /**
     * Maps a request DTO to an entity.
     *
     * @param dto the request DTO to convert
     * @return the resulting entity
     */
    E requestDtoMapToEntity(R dto);

    /**
     * Maps a response DTO to an entity.
     *
     * @param dto the response DTO to convert
     * @return the resulting entity
     */
    E responseDtoMapToEntity(S dto);

    /**
     * Maps a list of entities to a list of request DTOs.
     *
     * @param entityList the list of entities to convert
     * @return a list of resulting request DTOs
     */
    List<R> mapToRequestDtoList(List<E> entityList);

    /**
     * Maps a list of entities to a list of response DTOs.
     *
     * @param entityList the list of entities to convert
     * @return a list of resulting response DTOs
     */
    List<S> mapToResponseDtoList(List<E> entityList);

    /**
     * Maps a list of request DTOs to a list of entities.
     *
     * @param dtoList the list of request DTOs to convert
     * @return a list of resulting entities
     */
    List<E> requestDtosMapToEntityList(List<R> dtoList);

    /**
     * Maps a list of response DTOs to a list of entities.
     *
     * @param dtoList the list of response DTOs to convert
     * @return a list of resulting entities
     */
    List<E> responseDtoListMapToEntityList(List<S> dtoList);

    /**
     * Updates an existing entity with data from a request DTO.
     *
     * @param dto the request DTO containing the new data
     * @param entity the entity to update
     */
    void updateEntityFromRequestDto(R dto, @MappingTarget E entity);

    /**
     * Updates an existing entity with data from a response DTO.
     *
     * @param dto the response DTO containing the new data
     * @param entity the entity to update
     */
    void updateEntityFromResponseDto(S dto, @MappingTarget E entity);

}
