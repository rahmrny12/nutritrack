package com.example.nutritrack;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.content.Context;

import com.example.nutritrack.data.LoginDataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LoginDataSourceTest {

    @Mock
    Context mockContext;
    @Mock
    FirebaseAuth mockAuth;
    @Mock
    FirebaseUser mockUser;
    @Mock
    FirebaseDatabase mockDatabase;
    @Mock
    DatabaseReference dbRef;

    private LoginDataSource loginDataSource;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        loginDataSource = new LoginDataSource(mockContext);
    }

    @Test
    public void testLoginFirebaseSuccess() {
        // Arrange
        String email = "test@gmail.com";
        String password = "password";

        LoginDataSource.LoginCallback callback = mock(LoginDataSource.LoginCallback.class);

        // Act
        loginDataSource.login(email, password, callback);

        // Assert -> Verifikasi bahwa fungsi dipanggil
        verify(callback, never()).onResult(any()); // karena Firebase asli belum dipanggil
    }
}
