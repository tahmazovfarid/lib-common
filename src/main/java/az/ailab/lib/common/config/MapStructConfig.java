package az.ailab.lib.common.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Common MapStruct configuration to be used across mappers.
 * <p>
 * This configuration defines global settings for all mappers that reference
 * it, primarily setting a policy to ignore unmapped target properties.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * @Mapper(
 *     componentModel = "spring",
 *     config = MapStructConfig.class
 * )
 * public interface UserMapper extends GenericMapper<User, UserRequest, UserResponse> {
 *     // Your mapping methods here
 * }
 * }
 * </pre>
 * </p>
 */
@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructConfig {

    // This is just a configuration interface, no methods required

}
