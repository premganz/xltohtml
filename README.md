# xltohtml

A Spring Boot application that provides a mini-language interpreter for generating dynamic web applications. Transform custom scripts into interactive HTML pages with real-time API integration.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/premganz/xltohtml.git
   cd xltohtml
   ```

2. **Navigate to the Spring Boot application:**
   ```bash
   cd clime-springboot
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - Open your web browser and navigate to: **http://localhost:8080**
   - The application will automatically redirect to the main interface

### Alternative Running Methods

You can also build and run the application using:

```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Package and run
mvn clean package
java -jar target/clime-springboot-0.0.1-SNAPSHOT.jar
```

## Application Features

### 1. Mini-Language Interpreter
- **Real-time script parsing** with live logging
- **Dynamic HTML generation** from custom scripts
- **API integration** with automatic endpoint creation
- **File output** - generated applications are saved to the `output/` directory

### 2. Web Interface
- **Live parsing logs** displayed on-screen during generation
- **Interactive script editor** with syntax highlighting
- **Real-time preview** of generated applications
- **Example scripts** for quick start

### 3. Output Management
- Generated files are **automatically saved** to the `output/` directory
- Files are **downloadable** through the web interface
- **Timestamped filenames** for easy organization

## Mini-Language Syntax

The mini-language uses a block-based structure to define web applications:

### Core Grammar

| Symbol | Description |
|--------|-------------|
| `/begin block, type <type>` | Start a new block of specified type |
| `/end block` | Close the current block |
| `/begin matter, <attributes>` | Start a matter section with attributes |
| `/end matter` | Close the current matter section |
| `//content//` | Text content delimiter |
| `v_<name>` | Variable placeholder |

### Block Types

#### 1. API Declaration Block
Defines the API endpoint and parameters:

```
/begin block, type api_declaration
/begin matter, signature getStudentDetails,[parameters] [v_id]
/end matter
/end block
```

This creates a REST endpoint: `/api/students/{id}`

#### 2. Output Structure Block
Defines the expected JSON response structure:

```
/begin block, type output_structure
/begin matter, output_type json,text //{name:v_student.name, id: v_student_id courses:[{id:v_course_id, name:v_course.name}]}//
/end matter
/end block
```

#### 3. Layout Block
Defines the visual layout and data presentation:

```
/begin block, type layout
/begin matter text //

-------------student details-----------------

student name: v_student.name student id: v_student_id

------------List of courses---------------
Course_id		Course_name
------------------------------------------
v_course_id		v_course_name

//
/end matter
/end block
```

### Variable System

Variables use the `v_` prefix and support:
- **Simple variables**: `v_student_id`, `v_course_name`
- **Object properties**: `v_student.name`, `v_course.duration`
- **Automatic binding** to API response data

## Example Usage

### Complete Example Script

```
/begin block, type api_declaration
/begin matter, signature getStudentDetails,[parameters] [v_id]
/end matter
/end block

/begin block, type output_structure
/begin matter, output_type json,text //{name:v_student.name, id: v_student_id courses:[{id:v_course_id, name:v_course.name}]}//
/end matter
/end block

/begin block, type layout
/begin matter text //

-------------student details-----------------

student name: v_student.name student id: v_student_id

------------List of courses---------------
Course_id		Course_name
------------------------------------------
v_course_id		v_course_name

//
/end matter
/end block
```

This script generates:
1. **API endpoint**: `/api/students/{id}`
2. **Input form** for entering student ID
3. **Dynamic display** showing student details and course list
4. **Interactive functionality** with real-time data fetching

## API Endpoints

The application provides several REST endpoints:

- `GET /` - Main application interface
- `POST /api/generate` - Generate HTML from mini-language script
- `GET /api/logs` - Server-Sent Events for real-time logging
- `GET /api/students/{id}` - Example student data endpoint (when generated)

## File Structure

```
xltohtml/
├── clime-springboot/          # Spring Boot application
│   ├── src/main/java/         # Java source code
│   ├── src/main/resources/    # Static resources and templates
│   └── pom.xml               # Maven configuration
├── output/                   # Generated HTML files
└── README.md                # This documentation
```

## Development

### Building from Source

```bash
cd clime-springboot
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   - Stop other applications using port 8080
   - Or change the port in `application.properties`

2. **Java version compatibility**
   - Ensure you're using Java 17 or higher
   - Check with: `java -version`

3. **Maven build failures**
   - Ensure Maven is properly installed
   - Try: `mvn clean install -U`

### Getting Help

If you encounter issues:
1. Check the application logs for error messages
2. Verify all prerequisites are installed
3. Try running with `mvn clean spring-boot:run`
4. Check the `output/` directory for generated files

## License

This project is open source. Please check the repository for license details.