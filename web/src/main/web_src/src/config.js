const isProdBuild = process.env.NODE_ENV === 'production';

const config = {
	services: {
		serviceklageBackend: '/rest/serviceklage'
	},
    // Force values in production build
    debug: isProdBuild
};

export default config;
