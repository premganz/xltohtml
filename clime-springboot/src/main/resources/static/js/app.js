// JavaScript for API interaction and dynamic content
async function fetchStudentData(studentId) {
    const studentDataDiv = document.getElementById('student-data');
    
    // Show loading state
    studentDataDiv.innerHTML = '<div class="loading">Loading student data...</div>';
    
    try {
        const response = await fetch(`/api/students/${studentId}`);
        
        if (!response.ok) {
            throw new Error(`Student not found (ID: ${studentId})`);
        }
        
        const student = await response.json();
        displayStudentData(student);
        
    } catch (error) {
        studentDataDiv.innerHTML = `<div class="error">Error: ${error.message}</div>`;
    }
}

function displayStudentData(student) {
    const studentDataDiv = document.getElementById('student-data');
    
    let coursesHtml = '';
    if (student.courses && student.courses.length > 0) {
        coursesHtml = '<h4>Enrolled Courses:</h4><ul class="courses-list">';
        student.courses.forEach(course => {
            coursesHtml += `
                <li class="course-item">
                    <div class="course-name">${course.name}</div>
                    <div class="course-duration">Course ID: ${course.id} | Duration: ${course.duration} hours</div>
                </li>
            `;
        });
        coursesHtml += '</ul>';
    } else {
        coursesHtml = '<p>No courses enrolled.</p>';
    }
    
    studentDataDiv.innerHTML = `
        <div class="student-card">
            <div class="student-name">${student.name}</div>
            <div class="student-id">Student ID: ${student.id}</div>
            ${coursesHtml}
        </div>
    `;
}

// Initialize the page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Application initialized');
    
    // You can add any initialization code here
    const studentDataDiv = document.getElementById('student-data');
    if (studentDataDiv) {
        studentDataDiv.innerHTML = '<div class="loading">Click a button above to load student data</div>';
    }
});