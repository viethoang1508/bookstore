package org.example.promotion_service.client;

import java.util.List;
import java.util.Set;

public interface BookClient {
    Set<String> checkIfBooksExist(List<String> bookIds);
}
