--//custom Exception /date 22-12-2021 before lunch spring-boot-training
//1)need to create package exception
//2)Definr your own class for exception (Usernot found)

	//A)
	package com.example.springboottraining.exception;
	public class UserNotFoundException extends RuntimeException {

	}
	
	//B)
	public class MyRandomException extends RuntimeException {
    private final String message;
    public MyRandomException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

//3)Create class for error response

public class MyRandomException extends RuntimeException {
    private final String message;
    public MyRandomException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

//4)need to create class for @controllerAdvice and use @ExceptionHandler  anotation to get above class and throw exception accordingly

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleUserNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(111, "User could not be found"));
    }

    @ExceptionHandler(MyRandomException.class)
    private ResponseEntity<ErrorResponse> handleMyRandomException(MyRandomException ex) {
        System.out.println("Exception Message: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(222, ex.getMessage()));
    }
}

//5)In controller directly throw exception eg new Throw UsernotFound()

  public ResponseEntity<User> getSingleUser(@PathVariable(name = "userId") Long id) {
        Optional<User> userFound = userRepo.findById(id);
        if (id.equals(Long.parseLong("11"))) {
            LOGGER.info("Throw Random Exception for: {}", id);
            throw new MyRandomException("Eleven is not allowed");
        }

        if (userFound.isPresent()) {
            LOGGER.info("User found with id: {}", id);
            return ResponseEntity.ok(userFound.get()); // 200
        }

        LOGGER.error("User not found with id: {}", id);
//        return ResponseEntity.notFound().build(); // 404
        throw new UserNotFoundException();
    }