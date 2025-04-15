package az.ailab.lib.common.model.dto.request;

import java.util.LinkedHashSet;

public interface SearchFilter {

    LinkedHashSet<String> getAllowedSortFields();

}
