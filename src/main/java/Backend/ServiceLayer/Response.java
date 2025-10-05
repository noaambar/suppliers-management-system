// package Backend.ServiceLayer;

// public class Response {
//     private String Message;
//    // private Object returnValue;

//     // Constructors
//     public Response() {
//     }

//     public Response(String errorMessage) {
//         this.Message = errorMessage;
//       //  this.returnValue = null;
//     }

//     public Response(String errorMessage, Object returnValue) {
//         this.Message = errorMessage;
//        // this.returnValue = returnValue;
//     }

//     // Getters and Setters
//     public String getErrorMessage() {
//         return Message;
//     }

//     public void setErrorMessage(String errorMessage) {
//         this.Message = errorMessage;
//     }

//     //public Object getReturnValue() {
// //        return returnValue;
// //    }

//   //  public void setReturnValue(Object returnValue) {
//  //       this.returnValue = returnValue;
//   //  }

//     // toString method
// //    @Override
//     public String toString() {
//         return this.Message;
//     }
// }
package Backend.ServiceLayer;

public class  Response <T>{

    public T responseValue;
    public String errorMessage;
    public boolean isError = false;

    public Response(T responseValue, String errorMessage) {
        this.responseValue = responseValue;
        this.errorMessage = errorMessage;
        if (errorMessage != null || responseValue == null) {
            this.isError = true;
        } else {
            this.isError = false;
            
        }
    }

    // public Response(T responseValue) {
    //     this.responseValue = responseValue;
    //     this.errorMessage = null;
    // }
    // public Response(String errorMessage) {
    //     this.responseValue = null;
    //     this.errorMessage = errorMessage;
    //     this.isError = true;
    // }
    public T getData() {
        return responseValue;
    }
    public String getError() {
        return errorMessage;
    }

    public boolean isError() {
        return isError;
    }

    @Override
    public String toString() {
        if (isError) {
            return "Error: " + errorMessage;
        } else {
            return responseValue.toString();
        }
    }

    
}
