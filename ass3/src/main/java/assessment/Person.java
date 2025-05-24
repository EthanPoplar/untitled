package assessment;

public abstract class Person //abstract class Person
{
    private String firstName;
    private String secondName;
    private int age;
    private String gender;

    public Person()
    {

    }

    public Person(String firstName, String secondName, int age, String gender)
    {
        if (firstName == null || secondName == null || age <= 0 || gender == null)
        {
            throw new IllegalArgumentException("All fields are required");
        }

        if(!gender.equals("Male") && !gender.equals("Woman") && !gender.equals("Non-Binary") &&  !gender.equals("Other"))
        {
            throw new IllegalArgumentException("Gender field options must be provided");
        }

        if(!Character.isLetter(firstName.charAt(0)))
        {
            throw new IllegalArgumentException("First name must start with letter");
        }

        if(!Character.isLetter(secondName.charAt(0)))
        {
            throw new IllegalArgumentException("Second name must start with letter");
        }

        this.age=age;
        this.firstName=firstName;
        this.secondName=secondName;
        this.gender=gender;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        if(!gender.equals("Male") && !gender.equals("Woman") && !gender.equals("Non-Binary") &&  !gender.equals("Other"))
        {
            throw new IllegalArgumentException("Gender field options must be provided");
        }

        this.gender = gender;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getSecondName()
    {
        return secondName;
    }

    public void setFirstName(String firstName)
    {
        System.out.println(firstName.charAt(0));
        if(!Character.isLetter(firstName.charAt(0)))
        {
            throw new IllegalArgumentException("First name must start with letter");
        }
        this.firstName = firstName;
    }

    public void setSecondName(String secondName)
    {
        if(!Character.isLetter(secondName.charAt(0)))
        {
            throw new IllegalArgumentException("Second name must start with letter");
        }
        this.secondName = secondName;
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}
