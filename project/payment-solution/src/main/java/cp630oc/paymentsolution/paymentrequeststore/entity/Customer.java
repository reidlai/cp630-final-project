package cp630oc.paymentsolution.paymentrequeststore.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Customer entity class to represent a customer in the database table customers. The 
 * class is annotated with JPA annotations to define the mapping between the Java 
 * class and the database table. The class contains the following attributes:
 * - id: Long
 * - firstName: String
 * - lastName: String
 * - email: String
 * - currentAge: int
 * - retirementAge: int
 * - birthYear: int
 * - birthMonth: int
 * - gender: String
 * - address: String
 * - latitude: float
 * - longitude: float
 * - perCapitaIncome: float
 * - yearlyIncome: float
 * - totalDebt: float
 * - creditScore: int
 * - creditCardCount: int
 * - cards: List of Card
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(name = "current_age", nullable = false)
    private int currentAge;

    @Column(name = "retirement_age", nullable = false)
    private int retirementAge;

    @Column(name = "birth_year", nullable = false)
    private int birthYear;

    @Column(name = "birth_month", nullable = false)
    private int birthMonth;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    @Column(name = "per_capita_income", nullable = false)
    private float perCapitaIncome;

    @Column(name = "yearly_income", nullable = false)
    private float yearlyIncome;

    @Column(name = "total_debt", nullable = false)
    private float totalDebt;

    @Column(name = "credit_score", nullable = false)
    private int creditScore;

    @Column(name = "credit_card_count", nullable = false)
    private int creditCardCount;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Card> cards;

    /**
     * Get the customer ID
     * @return customer ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the customer ID
     * @param id Customer ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the customer first name
     * @return Customer first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the customer first name
     * @param firstName Customer first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the customer last name
     * @return Customer last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the customer last name
     * @param lastName Customer last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the customer email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the customer email
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get the current age
     * @return current age
     */
    public int getCurrentAge() {
        return currentAge;
    }

    /**
     * Set the current age
     * @param currentAge current age
     */
    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    /**
     * Get the retirement age
     * @return retirement age
     */
    public int getRetirementAge() {
        return retirementAge;
    }

    /**
     * Set the retirement age
     * @param retirementAge retirement age
     */
    public void setRetirementAge(int retirementAge) {
        this.retirementAge = retirementAge;
    }

    /**
     * Get the birth year
     * @return birth year
     */
    public int getBirthYear() {
        return birthYear;
    }

    /**
     * Set the birth year
     * @param birthYear birth year
     */
    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    /**
     * Get the birth month
     * @return birth month
     */
    public int getBirthMonth() {
        return birthMonth;
    }

    /**
     * Set the birth month
     * @param birthMonth birth month
     */
    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    /**
     * Get gender
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set gender
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get the address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address
     * @param address address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the latitude
     * @return latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude
     * @param latitude latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the longitude
     * @return longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude
     * @param longitude longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the per capita income
     * @return per capita income
     */
    public float getPerCapitaIncome() {
        return perCapitaIncome;
    }

    /**
     * Set the per capita income
     * @param perCapitaIncome per capita income
     */
    public void setPerCapitaIncome(float perCapitaIncome) {
        this.perCapitaIncome = perCapitaIncome;
    }

    /**
     * Get the yearly income
     * @return the yearly income
     */
    public float getYearlyIncome() {
        return yearlyIncome;
    }

    /**
     * Set the yearly income
     * @param yearlyIncome the yearly income
     */
    public void setYearlyIncome(float yearlyIncome) {
        this.yearlyIncome = yearlyIncome;
    }

    /**
     * Get the total debt
     * @return the total debt
     */
    public float getTotalDebt() {
        return totalDebt;
    }

    /**
     * Set the total debt
     * @param totalDebt the total debt
     */
    public void setTotalDebt(float totalDebt) {
        this.totalDebt = totalDebt;
    }

    /**
     * Get the credit score
     * @return the credit score
     */
    public int getCreditScore() {
        return creditScore;
    }

    /**
     * Set the credit score
     * @param creditScore the credit score
     */
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    /**
     * Get the credit card count
     * @return the credit card count
     */
    public int getCreditCardCount() {
        return creditCardCount;
    }

    /**
     * Set the credit card count
     * @param creditCardCount the credit card count
     */
    public void setCreditCardCount(int creditCardCount) {
        this.creditCardCount = creditCardCount;
    }

    /**
     * Get the cards
     * @return List of Cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Set the cards
     * @param cards List of Cards 
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}