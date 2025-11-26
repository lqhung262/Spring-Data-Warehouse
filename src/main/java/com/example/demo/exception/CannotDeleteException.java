package com.example.demo.exception;

/**
 * Exception thrown when attempting to delete an entity that is still referenced by other entities.
 * This enforces referential integrity at the application level.
 */
public class CannotDeleteException extends RuntimeException {
    public CannotDeleteException(String message) {
        super(message);
    }

    public CannotDeleteException(String entityName, Long id, String referencingEntity, long count) {
        super(String.format(
                "Cannot delete %s with ID %d because %d %s(s) are still referencing it. " +
                        "Please delete or reassign the referencing records first.",
                entityName, id, count, referencingEntity
        ));
    }
}

