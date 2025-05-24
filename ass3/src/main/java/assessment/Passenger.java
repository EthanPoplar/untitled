package assessment;

import java.util.regex.Pattern;

public class Passenger extends Person {
    private String email;
    private String phoneNumber;
    private String cardNumber;
    private int securityCode;
    private String passport;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9]+@[A-Za-z0-9]+\\.com$");
    private static final Pattern PASSPORT_AU = Pattern.compile("^[A-Z][0-9]{7}$");
    private static final Pattern PASSPORT_NZ = Pattern.compile("^[A-Z]{2}[0-9]{7}$");
    private static final Pattern PASSPORT_US = Pattern.compile("^[0-9]{9}$");

    public Passenger() {

    }

    public Passenger(String firstName, String secondName, int age, String gender, String email, String phoneNumber, String passport, String cardNumber, int securityCode)
    {
        super();

        if (firstName == null || secondName == null || age <= 0 || gender == null || email == null || phoneNumber == null || passport == null || cardNumber == null || securityCode <= 0)
        {
            throw new IllegalArgumentException("All fields are required");
        }

        if(!phoneNumber.startsWith("+61") && !phoneNumber.startsWith("04") && !phoneNumber.startsWith("+64") && !phoneNumber.startsWith("+1"))
        {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if(phoneNumber.startsWith("04"))
        {
            phoneNumber = "+61 " + phoneNumber.substring(1);
        }

        if(!EMAIL_PATTERN.matcher(email).matches())
        {
            throw new IllegalArgumentException("Invalid email format");
        }

        if(!PASSPORT_NZ.matcher(passport).matches() && !PASSPORT_US.matcher(passport).matches() && !PASSPORT_AU.matcher(passport).matches())
        {
            throw new IllegalArgumentException("Invalid passport number format");
        }

        this.securityCode = securityCode;
        this.cardNumber = cardNumber;
        this.passport = passport;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email)
    {
        if(!EMAIL_PATTERN.matcher(email).matches())
        {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.email = email;
    }

    public String getFirstName() {
        return super.getFirstName();
    }

    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
    }

    public String getSecondName() {
        return super.getSecondName();
    }

    public void setSecondName(String secondName) {
        super.setSecondName(secondName);
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport)
    {
        if(!PASSPORT_NZ.matcher(passport).matches() && !PASSPORT_US.matcher(passport).matches() && !PASSPORT_AU.matcher(passport).matches())
        {
            throw new IllegalArgumentException("Invalid passport number format");
        }

        this.passport = passport;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        if(!phoneNumber.startsWith("+61") && !phoneNumber.startsWith("04") && !phoneNumber.startsWith("+64") && !phoneNumber.startsWith("+1"))
        {
            throw new IllegalArgumentException("Invalid phone number");
        }

        this.phoneNumber = phoneNumber;
    }

    public int getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(int securityCode) {
        this.securityCode = securityCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String getGender() {
        return super.getGender();
    }

    public void setGender(String gender) {
        super.setGender(gender);
    }

    @Override
    public int getAge() {
        return super.getAge();
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
    }

    @Override
    public String toString() {
        return "Passenger{" + " Fullname= " + super.getFirstName() + " " + super.getSecondName() +
                " ,email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", passport='" + passport +
                '}';
    }
}
