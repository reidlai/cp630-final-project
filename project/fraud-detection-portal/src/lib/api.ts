import type { PaymentRequest, PaymentRequestStatus, UpdatePaymentRequestStatusByIdRequest } from './types';

const API_BASE = 'http://localhost:8080';

export async function searchTransactions(cardNumber: string): Promise<PaymentRequest[]> {
    console.debug('Searching transactions', { cardNumber });
    const response = await fetch(`${API_BASE}/payment-requests?card_number=${cardNumber}`);
    if (!response.ok) throw new Error('Failed to fetch transactions');
    return response.json();
}

export async function getTransactionStatus(id: string): Promise<PaymentRequestStatus> {
    const response = await fetch(`${API_BASE}/payment-request-status?id=${id}`);
    if (!response.ok) throw new Error('Failed to fetch transaction status');
    return response.json();
}

export async function updateTransactionStatus(request: UpdatePaymentRequestStatusByIdRequest): Promise<PaymentRequestStatus> {
    const response = await fetch(`${API_BASE}/payment-request-status`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
    });
    if (!response.ok) throw new Error('Failed to update transaction status');
    return response.json();
}

// export async function searchTransactions(cardNumber: string): Promise<PaymentRequest[]> {
//     try {
//         const response = await fetch(`${API_BASE}/payment-requests?card_number=${cardNumber}`);
//         if (!response.ok) {
//             const errorText = await response.text();
//             throw new Error('Failed to fetch transactions');
//         }
//         const data = await response.json();
//         return data;
//     } catch (error) {
//         throw error;
//     }
// }

// export async function getTransactionStatus(id: string): Promise<PaymentRequestStatus> {
//     try {
//         const response = await fetch(`${API_BASE}/payment-request-status?id=${id}`);
//         if (!response.ok) {
//             const errorText = await response.text();
//             throw new Error('Failed to fetch transaction status');
//         }
//         const data = await response.json();
//         return data;
//     } catch (error) {
//         throw error;
//     }
// }

// export async function updateTransactionStatus(request: UpdatePaymentRequestStatusByIdRequest): Promise<PaymentRequestStatus> {
//     try {
//         const response = await fetch(`${API_BASE}/payment-request-status`, {
//             method: 'PUT',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(request)
//         });
//         if (!response.ok) {
//             const errorText = await response.text();
//             throw new Error('Failed to update transaction status');
//         }
//         const data = await response.json();
//         return data;
//     } catch (error) {
//         throw error;
//     }
// }