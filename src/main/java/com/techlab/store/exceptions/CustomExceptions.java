package com.techlab.store.exceptions;

public class CustomExceptions {

    /** -- Product Excepcions -- **/

    public static class ProductHasDeletedException extends RuntimeException {
        // Constructor con mensaje personalizado
        public ProductHasDeletedException(String message) {
            super(message);
        }
        public ProductHasDeletedException(Long id) {
            super("❌ El producto eliminado con " + id + " solo se puede leer");
        }
    }


    public static class ProductNotFoundException extends RuntimeException {
        // Constructor con mensaje personalizado
        public ProductNotFoundException(String message) {
            super(message);
        }

        // Constructor sin mensaje (usa el predeterminado)
        public ProductNotFoundException() {
            super("❌ El recurso Listing no fue encontrado.");
        }
        
        // Opcional: Constructor con ID para generar el mensaje automáticamente
        public ProductNotFoundException(Long id) {
            super("❌ El Listing con ID " + id + " no fue encontrado.");
        }
    }



    /** -- Listing Excepcions -- **/

    public static class ListingNotFoundException extends RuntimeException {
        // Constructor con mensaje personalizado
        public ListingNotFoundException(String message) {
            super(message);
        }

        // Constructor sin mensaje (usa el predeterminado)
        public ListingNotFoundException() {
            super("❌ El recurso Listing no fue encontrado.");
        }
        
        // Opcional: Constructor con ID para generar el mensaje automáticamente
        public ListingNotFoundException(Long id) {
            super("❌ El Listing con ID " + id + " no fue encontrado.");
        }
    }


    public static class ListingHasDeletedException extends RuntimeException {
        public ListingHasDeletedException() {
            super("❌ El listing ha sido eliminado");
        }
        public ListingHasDeletedException(Long id) {
            super("❌ El listing con Id "+ id + "ha sido eliminado");
        }
    }

    public static class ListingUpdatedException extends RuntimeException {
        public ListingUpdatedException(String message) {
            super(message);
        }

        public ListingUpdatedException(Long id) {
            super("❌ Error al actualizar. El listing con ID: " + id + " no fue encontrado.");
        }
    }



    /** -- Image Excepcions -- **/

    public static class ImageNotFoundException extends RuntimeException {
        public ImageNotFoundException() {
            super("❌ La imagen especificada no se encontró en el listado.");
        }
        
        public ImageNotFoundException(String imageUrl) {
            super("❌ La imagen con URL '" + imageUrl + "' no se encontró.");
        }
    }



    /** -- Storage Excepcions -- **/

    public static class StorageException extends RuntimeException {
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }

        public StorageException(String message) {
            super(message);
        }
        
        public StorageException() {
            super("Ocurrió un error al intentar almacenar o eliminar un archivo.");
        }
    }

    public static class ReviewExpiratedException extends RuntimeException {
        public ReviewExpiratedException(String message, Throwable cause) {
            super(message, cause);
        }

        public ReviewExpiratedException (Long id) {
            super("❌ La solicitud de review expiro o no es valida");
        }
    }



    /** -- User Excepcions -- **/

    public static class UserHasDeletedException extends RuntimeException {
        public UserHasDeletedException() {
            super("❌ El usuario ha sido eliminado");
        }
        public UserHasDeletedException(Long id) {
            super("❌ El usuario con Id "+ id + "ha sido eliminado");
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        // Constructor con mensaje personalizado
        public UserNotFoundException(String message) {
            super(message);
        }

        // Constructor sin mensaje (usa el predeterminado)
        public UserNotFoundException() {
            super("❌ El User no fue encontrado.");
        }
        
        // Opcional: Constructor con ID para generar el mensaje automáticamente
        public UserNotFoundException(Long id) {
            super("❌ El User con ID " + id + " no fue encontrado.");
        }
    }
}
