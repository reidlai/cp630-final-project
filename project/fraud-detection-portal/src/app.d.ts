/// <reference types="@sveltejs/kit" />

// See https://kit.svelte.dev/docs/types#app
declare global {
	namespace App {
			// interface Error {}
			interface Locals {
					// Add any custom properties you want available in server-side hooks
			}
			interface PageData {
					// Add any custom properties you want available in page data
			}
			// interface Platform {}
	}

	// Add your custom types here
	interface PaymentRequest {
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

	interface PaymentRequestStatus {
			id: string;
			state: string;
			createdAt: string;
			updatedAt: string;
			deletedAt?: string;
	}

	interface UpdatePaymentRequestStatusByIdRequest {
			id: string;
			state: string;
	}
}

// Keep this empty export to mark this as a module
export {};