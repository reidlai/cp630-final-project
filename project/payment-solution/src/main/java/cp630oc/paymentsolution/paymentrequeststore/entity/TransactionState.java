package cp630oc.paymentsolution.paymentrequeststore.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * The transaction entity.
 */
@Entity
@Table(name = "transaction_states")
public class TransactionState {

    @EmbeddedId
    private TransactionStateId id;

    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private Transaction transaction;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedAt", nullable = false)
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deletedAt", nullable = true)
    private Date deletedAt;

    /**
     * Get the transaction state ID.
     * @return the transaction state ID
     */
    public TransactionStateId getId() {
        return id;
    }

    /**
     * Set the transaction state ID.
     * @param id the transaction state ID
     */
    public void setId(TransactionStateId id) {
        this.id = id;
    }

    /**
     * Get the transaction.
     * @return the transaction
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Set the transaction.
     * @param transaction the transaction
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Get the creation date and time.
     * @return the creation date and time
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation date and time.
     * @param createdAt the creation date and time
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the update date and time.
     * @return the transaction date and time
     */
    public Date getUpdatedAt() {
        return createdAt;
    }

    /**
     * Set the update date and time.
     * @param updatedAt the update date and time
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Get the deletion date and time.
     * @return the deletion date and time
     */
    public Date getDeletedAt() {
        return deletedAt;
    }

    /**
     * Set the deletion date and time.
     * @param deletedAt the deletion date and time
     */
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }


}