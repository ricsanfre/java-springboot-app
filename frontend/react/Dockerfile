FROM node:19-alpine
ARG BACKEND_URL=http://localhost:8080

WORKDIR /app
# Install node modules
COPY package*.json .
RUN npm i --silent
COPY . .
RUN echo "VITE_API_BASE_URL=$BACKEND_URL" > .env
EXPOSE 5173
CMD ["npm","run","dev"]