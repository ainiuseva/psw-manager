package lt.viko.eif.entity;

import java.util.Objects;

public class UserPassword {

    private String title;

    private String password;

    public UserPassword(String title, String password) {
        this.title = title;
        this.password = password;
    }

    public UserPassword() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserPassword userPassword1 = (UserPassword) o;
        return Objects.equals(title, userPassword1.title)
                && Objects.equals(password, userPassword1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, password);
    }

    @Override
    public String toString() {
        return "Password{" +
                "title='" + title + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
