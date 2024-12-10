import { writable } from 'svelte/store';
import type { PaymentRequest } from '$lib/types';

interface SearchState {
    cardNumber: string;
    transactions: PaymentRequest[];
}

const createSearchStore = () => {
    const { subscribe, set, update } = writable<SearchState>({
        cardNumber: '',
        transactions: []
    });

    return {
        subscribe,
        setSearch: (cardNumber: string, transactions: PaymentRequest[]) => {
            set({ cardNumber, transactions });
        },
        reset: () => {
            set({ cardNumber: '', transactions: [] });
        }
    };
};

export const searchStore = createSearchStore();