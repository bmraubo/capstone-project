FROM rust:1.59 as builder

WORKDIR /usr/src/user-service
COPY . .

RUN cargo install --path .

FROM debian:buster-slim
RUN apt-get update && rm -rf /var/lib/apt/lists/*
COPY --from=builder /usr/local/cargo/bin/user-service /usr/local/bin/user-service
COPY Rocket.toml .
EXPOSE 7000

CMD ["user-service"]