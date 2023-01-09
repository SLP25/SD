package view;

import utils.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class CommandLine {

    public void shell(Class service, Object instance) throws ClassNotFoundException, IOException {
        Method[] methods = service.getMethods();
        String[] input;
        do {
            Output.prompt("user", service.getName());
            input = Input.read();
            if (input[0].equals("help")) showMethods(methods);
            else if (input[0].equals("clear")) Output.clear();
            else callMethod(instance, methods, input);

        } while (!Input.isExit(input[0]));
    }

    private void showMethods(Method[] methods) throws ClassNotFoundException {
        for (Method method : methods) {
            Output.show(method.getName());
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                for (String string :
                        Arrays.asList(" ", parameter.getType().getName().replace("java.lang.", ""))) {
                    Output.show(string);
                }
            }
            Output.showln(" ");
        }
        Output.showln("quit");
    }

    private void callMethod(Object instance, Method[] methods, String[] input) {
        try {
            if (!Input.isExit(input[0])) {
                for (Method method : methods)
                    if (method.getName().equals(input[0])) {
                        Output.showln(method.invoke(
                                instance,
                                ReflectionUtils.argsGenerator(
                                    Arrays.copyOfRange(input, 1, input.length), method.getParameterTypes())));
                    }
            }
        } catch (InstantiationException
                 | NoSuchMethodException
                 | IllegalAccessException
                 | ArrayIndexOutOfBoundsException e) {
            Output.showln("Invalid Option");
        } catch( InvocationTargetException e) {
            Output.showln(e.getTargetException().getMessage());
        }
    }

}
