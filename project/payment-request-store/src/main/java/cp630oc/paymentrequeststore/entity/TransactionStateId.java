package cp630oc.paymentrequeststore.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

/**
 * The transaction state composite primary key.
 */
@Embeddable
public class TransactionStateId implements Serializable {
    
    /**
     * The transaction id from the transaction entity.
     */
    @Column(name = "id")
    private Long id; 
    
    /**
     * The state of the transaction.
     */
    @Column(name = "state")
    private String state;
    
    /**
     * Default constructor.
     */
    public TransactionStateId() {}
    
    /**
     * Instantiates a new transaction state id with the given id and state.
     *
     * @param id the id
     * @param state the state
     */
    public TransactionStateId(Long id, String state) {
        this.id = id;
        this.state = state;
    }
  
    /**
     * equals method to compare two objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionStateId that = (TransactionStateId) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(state, that.state);
    }

    /**
     * hashCode method to generate a hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, state);
    }

    /**
     * Get the id of the transaction.
     * @return Transaction ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the transaction.
     * @param id Transaction ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the transaction state.
     * @return the transaction state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the transaction state.
     * @param state the transaction date and time
     */
    public void setState(String state) {
        this.state = state;
    }

}
