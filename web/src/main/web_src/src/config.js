const isProdBuild = process.env.NODE_ENV === 'production';

const config = {
	services: {
		serviceklageBackend: '/rest/serviceklage'
	},
	debug: true
};

// Force values in production build
if (isProdBuild) {
	config.debug = false
}

export default config;
