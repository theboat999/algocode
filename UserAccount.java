    import java.util.List;

    public class UserAccount {
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String email;
        private String birthdate;

        public UserAccount(String firstName, String lastName, String username, String password, String email, String birthdate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.email = email;
            this.birthdate = birthdate;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public List<Book> getBookList() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getBookList'");
        }
    }
