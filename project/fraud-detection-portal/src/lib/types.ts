export interface PaymentRequest {
  id: string;
  transactionAmount: number;
  transactionDatetime: string;
  transactionType: string;
  merchantId: string;
  merchantCity: string;
  merchantState: string;
  merchantZip: string;
  merchantMccCode: string;
  fraudDetected: boolean;
}

export interface PaymentRequestStatus {
  id: string;
  state: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export interface UpdatePaymentRequestStatusByIdRequest {
  id: string;
  state: string;
}