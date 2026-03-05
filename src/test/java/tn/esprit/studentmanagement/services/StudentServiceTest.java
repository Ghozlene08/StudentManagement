package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents_shouldReturnListOfStudents() {
        Student s1 = new Student();
        s1.setIdStudent(1L);
        s1.setFirstName("Jean");
        s1.setLastName("Dupont");
        List<Student> expected = Arrays.asList(s1);

        when(studentRepository.findAll()).thenReturn(expected);

        List<Student> result = studentService.getAllStudents();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.get(0).getLastName()).isEqualTo("Dupont");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentById_whenExists_shouldReturnStudent() {
        Student student = new Student();
        student.setIdStudent(1L);
        student.setFirstName("Marie");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getIdStudent()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Marie");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_whenNotExists_shouldReturnNull() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        Student result = studentService.getStudentById(999L);

        assertThat(result).isNull();
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    void saveStudent_shouldReturnSavedStudent() {
        Student toSave = new Student();
        toSave.setFirstName("Paul");
        toSave.setLastName("Martin");
        Student saved = new Student();
        saved.setIdStudent(1L);
        saved.setFirstName("Paul");
        saved.setLastName("Martin");
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        Student result = studentService.saveStudent(toSave);

        assertThat(result).isNotNull();
        assertThat(result.getIdStudent()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Paul");
        verify(studentRepository, times(1)).save(toSave);
    }

    @Test
    void deleteStudent_shouldCallRepository() {
        studentService.deleteStudent(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }
}
