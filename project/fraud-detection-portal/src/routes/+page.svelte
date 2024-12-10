<script>
    import { goto } from '$app/navigation';
    import SearchBox from '$lib/components/SearchBox.svelte';
    import TransactionTable from '$lib/components/TransactionTable.svelte';
    import { searchTransactions } from '$lib/api';
    import { searchStore } from '$lib/stores/searchStore';

    let transactions = [];
    let error = null;
    let loading = false;

    // Subscribe to the store
    $: transactions = $searchStore.transactions;

    async function handleSearch(event) {
        try {
            loading = true; 
            error = null;
            transactions = await searchTransactions(event.detail);
            searchStore.setSearch(event.detail, transactions);
        } catch (e) {
            error = e.message;
            transactions = [];
        } finally {
            loading = false; // Set loading to false when search completes or fails
        }
    }

    function handleSelect(event) {
        goto(`/transaction/${event.detail}`);
    }
</script>

<h1>Fraud Detection Portal</h1>

<SearchBox initialValue={$searchStore.cardNumber} on:search={handleSearch} />

{#if loading}
    <div class="flex items-center justify-center p-8">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        <p class="ml-3 text-gray-600">Searching transactions...</p>
    </div>
{:else if error}
    <div class="bg-red-50 border border-red-200 rounded-lg p-4 mt-4 text-red-700">
        {error}
    </div>
{:else if transactions.length > 0}
    <TransactionTable 
        {transactions} 
        on:select={handleSelect}
    />
{/if}
