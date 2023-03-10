FROM node:10-alpine

WORKDIR /app

COPY package.json .
COPY package-lock.json .

RUN npm install --save --legacy-peer-deps

COPY . .

EXPOSE 3000

CMD ["npm", "start"]