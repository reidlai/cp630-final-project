<script lang="ts">
    import { onMount } from 'svelte';
    import type { PaymentRequest } from '$lib/types';
    
    export let transactionId: string;

    let transaction: PaymentRequest | null = null;
    let error: string | null = null;
    let loading = true;

    onMount(async () => {
        try {
            const transactionData = await fetch(`http://localhost:8080/payment-request?id=${transactionId}`).then(r => r.json());  
            transaction = transactionData;
            loading = false;
        } catch (e) {
            error = e instanceof Error ? e.message : 'An error occurred';
            loading = false;
        }
    });

</script>
<div class="max-w-4xl mx-auto p-4 space-y-6">
    <!-- Loading State -->
    {#if loading}
        <div class="flex justify-center items-center h-32">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
    <!-- Error State -->
    {:else if error}
        <div class="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
            <p>{error}</p>
            <button 
                class="mt-2 text-sm text-red-600 hover:text-red-800 underline"
                on:click={() => window.location.reload()}
            >
                Retry
            </button>
        </div>
    <!-- Content -->
    {:else}
        <!-- Payment Request Details  -->
        <div class="bg-white rounded-lg p-6 mb-8 shadow-md">
            <h3 class="text-gray-700 mb-4 border-b-2 border-gray-200 pb-2">Payment Request Details</h3>
            <div class="grid grid-cols-2 gap-y-3 gap-x-8 items-center">
                <div class="font-medium text-gray-700">Transaction ID:</div>
                <div class="text-gray-900">{transaction.id}</div>
                
                <div class="font-medium text-gray-700">Amount:</div>
                <div class="text-gray-900">${transaction.transactionAmount}</div>
                
                <div class="font-medium text-gray-700">Date:</div>
                <div class="text-gray-900">{new Date(transaction.transactionDatetime).toLocaleString()}</div>
                
                <div class="font-medium text-gray-700">Type:</div>
                <div class="text-gray-900">{transaction.transactionType}</div>
                
                <div class="font-medium text-gray-700">Merchant:</div>
                <div class="text-gray-900">
                    {transaction.merchantId}<br>
                    <span class="text-sm text-gray-500">{transaction.merchantMccCode}</span>
                </div>
                
                <div class="font-medium text-gray-700">Location:</div>
                <div class="text-gray-900">
                    {transaction.merchantCity}, {transaction.merchantState} {transaction.merchantZip}
                </div>
                
                <div class="font-medium text-gray-700">Fraud Detection:</div>
                <div class="text-gray-900">
                    <span class={transaction.fraudDetected ? 'text-red-600 font-bold' : 'text-green-600'}>
                        {transaction.fraudDetected ? 'Yes' : 'No'}
                    </span>
                </div>
            </div>
        </div>
    {/if}
</div>