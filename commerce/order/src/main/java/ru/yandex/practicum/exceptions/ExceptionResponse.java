package ru.yandex.practicum.exceptions;

import java.util.List;

public class ExceptionResponse {
    private Cause cause;

    public ExceptionResponse(Cause cause) {
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    public static class Cause {
        private List<StackTraceElementDto> stackTrace;
        private String message;
        private String localizedMessage;

        public Cause(List<StackTraceElementDto> stackTrace, String message, String localizedMessage) {
            this.stackTrace = stackTrace;
            this.message = message;
            this.localizedMessage = localizedMessage;
        }

        public List<StackTraceElementDto> getStackTrace() {
            return stackTrace;
        }

        public String getMessage() {
            return message;
        }

        public String getLocalizedMessage() {
            return localizedMessage;
        }
    }

    public static class StackTraceElementDto {
        private String classLoaderName;
        private String moduleName;
        private String moduleVersion;
        private String methodName;
        private String fileName;
        private int lineNumber;
        private String className;
        private boolean nativeMethod;

        public StackTraceElementDto(StackTraceElement element) {
            this.classLoaderName = null;
            this.moduleName = element.getModuleName();
            this.moduleVersion = element.getModuleVersion();
            this.methodName = element.getMethodName();
            this.fileName = element.getFileName();
            this.lineNumber = element.getLineNumber();
            this.className = element.getClassName();
            this.nativeMethod = element.isNativeMethod();
        }

        public String getClassLoaderName() {
            return classLoaderName;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getModuleVersion() {
            return moduleVersion;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getClassName() {
            return className;
        }

        public boolean isNativeMethod() {
            return nativeMethod;
        }
    }
}

