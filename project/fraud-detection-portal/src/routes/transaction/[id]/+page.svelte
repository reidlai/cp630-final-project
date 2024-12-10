<script>
    import { page } from '$app/stores';
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';

    import PaymentRequestDetails from '$lib/components/PaymentRequestDetails.svelte';
    import PaymentRequestStateHistory from '$lib/components/PaymentRequestStateHistory.svelte';

    const id = $page.params.id;
    
    onMount(async () => {});

</script>

<div class="container mx-auto p-4">
    <button class="back mb-4 text-blue-500 hover:text-blue-700" on:click={() => goto('/')}>← Back to List</button>
    <PaymentRequestDetails transactionId={id} />
    <PaymentRequestStateHistory transactionId={id} />
</div>

