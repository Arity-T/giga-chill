FROM cypress/base:22.16.0

WORKDIR /tests

COPY package.json package-lock.json .
RUN npm ci

COPY . .

CMD ["npm", "run", "test"]