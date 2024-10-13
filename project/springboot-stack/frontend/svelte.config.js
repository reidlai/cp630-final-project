import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	extensions: ['.svelte'],
	preprocess: [vitePreprocess()],
	
	vitePlugin: {
		inspector: true,
	},
	kit: {
		// Use the static adapter for building a static site
		adapter: adapter({
			pages: 'build', // Directory for built pages
			assets: 'build', // Directory for built assets
			fallback: 'index.html', // Fallback for SPA
			precompress: false,
			strict: true
		}),
		prerender: { entries: [] }, // Specify entries to prerender if needed
	}
};

export default config;