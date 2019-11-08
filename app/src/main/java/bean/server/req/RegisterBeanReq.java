package bean.server.req;

/**
 * Created by john on 2019/4/27.
 */
public class RegisterBeanReq {
    private String userName;
    private String userTitle;
    private String contactName;
    private String telephoneNumber;
    private String officeTelephoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getOfficeTelephoneNumber() {
        return officeTelephoneNumber;
    }

    public void setOfficeTelephoneNumber(String officeTelephoneNumber) {
        this.officeTelephoneNumber = officeTelephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegisterBeanReq{" +
                "userName='" + userName + '\'' +
                ", userTitle='" + userTitle + '\'' +
                ", contactName='" + contactName + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", officeTelephoneNumber='" + officeTelephoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
