import type { PaymentRequest, PaymentRequestStatus, UpdatePaymentRequestStatusByIdRequest } from './types';

// Payment Solutoin API Endpoint
const API_BASE = import.meta.env.VITE_PAYMENT_REQUEST_API_URL;

const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Authorization': `Bearer ${import.meta.env.VITE_API_TOKEN}`,
    'X-Notification': 'true',
};

export async function searchTransactions(cardNumber: string): Promise<PaymentRequest[]> {
    try {
        const response = await fetch(`${API_BASE}/payment-requests?card_number=${cardNumber}`, {
            method: 'GET',
            headers: headers,
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        throw error;
    }
}

export async function getTransactionDetail(id: string): Promise<PaymentRequest> {
    try {
        const response = await fetch(`${API_BASE}/payment-request?id=${id}`, {
            method: 'GET',
            headers: headers,
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        throw error;
    }

}

export async function getTransactionStateHistory(id: string): Promise<PaymentRequestStatus> {
    try {
        const response = await fetch(`${API_BASE}/payment-request-statuses?id=${id}`, {
            method: 'GET',
            headers: headers
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }
        const data = await response.json();
        // return data
        // Sort the statuses by deletedAt date
        const statuses = data.sort((a, b) => {
            if (a.deletedAt === null && b.deletedAt !== null) {
                return -1;
            }
            if (a.deletedAt !== null && b.deletedAt === null) {
                return 1;
            }
            if (a.deletedAt !== null && b.deletedAt !== null) {
                return new Date(b.deletedAt).getTime() - new Date(a.deletedAt).getTime();
            }
            return 0;
        });
        
        return statuses;
    } catch (error) {
        throw error;
    }
}

export async function updateTransactionState(request: UpdatePaymentRequestStatusByIdRequest): Promise<PaymentRequestStatus> {
    try {
        const response = await fetch(`${API_BASE}/payment-request-status`, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(request)
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        throw error;
    }

}
