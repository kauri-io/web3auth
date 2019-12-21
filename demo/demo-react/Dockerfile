# base image
FROM node:12.2.0-alpine

# install software
RUN apk update && apk add --no-cache git bash jq

# set working directory
WORKDIR /app

# install truffle
RUN npm install -g truffle && npm config set bin-links false

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

# Copy all the content
COPY . .

# install dependencies
RUN yarn install --silent

# make run executable
RUN chmod +x /app/run.sh

# run...
CMD ["/app/run.sh"]
