# CORBA String Operations

This is a distributed application using CORBA to demonstrate object brokering with string operations.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- CORBA tools (idlj, orbd)

## Compilation Steps

1. Compile the IDL file to generate Java stubs and skeletons:
   ```bash
   idlj -fall StringOperations.idl
   ```

2. Compile all Java files:
   ```bash
   javac StringOperations/*.java && javac *.java
   ```

## Execution Steps

1. Start the naming service (orbd) in a separate terminal:
   ```bash
   orbd -ORBInitialPort 1050
   ```

2. Start the server in a separate terminal:
   ```bash
   java StringServer -ORBInitialPort 1050 -ORBInitialHost localhost
   ```

3. Run the client in a separate terminal:
   ```bash
   java StringClient -ORBInitialPort 1050 -ORBInitialHost localhost
   ```

## Troubleshooting

### Common Compilation Errors

1. **Cannot find symbol: class StringServicePOA**
   - Make sure you've run `idlj -fall StringOperations.idl` first
   - Check that the StringOperations directory was created with the generated files
   - Verify that your Java version supports CORBA (JDK 8 is recommended)

2. **Method does not override or implement a method from a supertype**
   - The implementation class should extend `Servant` and implement `StringServiceOperations`
   - Remove `@Override` annotations if they're causing issues

3. **Incompatible types: StringOperationsImpl cannot be converted to Servant**
   - Make sure your implementation class extends `Servant`

### Runtime Errors

1. **Connection errors**
   - Make sure the orbd service is running
   - Check that you're using the correct port number
   - Verify that the ORBInitialHost parameter is set correctly

2. **Name not found errors**
   - Make sure the server is running and has registered the service
   - Check that the service name matches between client and server

3. **ClassNotFoundException**
   - Make sure all classes are in the classpath
   - Check that you've compiled all the necessary files

## Expected Output

The client will perform a series of string operations and display the results:

```
(main) : Testing string operations...
(main) : Concatenation result: HelloWorld
(main) : Reverse result: dlroWolleH
(main) : Uppercase result: DLROWOLLEH
(main) : Lowercase result: dlrowolleh
(main) : Length: 10
(main) : Contains 'world': true
(main) : Substring result: dlrow
```

The server will log each operation as it receives requests:

```
(main) : StringService is ready and waiting...
(main) : Press Ctrl+C to exit
(Thread-1) : Concatenating strings: Hello and World
(Thread-1) : Reversing string: HelloWorld
(Thread-1) : Converting to uppercase: dlroWolleH
(Thread-1) : Converting to lowercase: DLROWOLLEH
(Thread-1) : Getting length of: dlrowolleh
(Thread-1) : Checking if dlrowolleh contains world
(Thread-1) : Getting substring of dlrowolleh from 0 to 5
``` 