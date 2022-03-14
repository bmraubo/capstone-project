FROM rust:1.59 as builder

WORKDIR /usr/src/gateway
COPY . .

RUN cargo install --path .

FROM debian:buster-slim
RUN apt-get update && rm -rf /var/lib/apt/lists/*
COPY --from=builder ../usr/local/cargo/bin/gateway ../usr/local/bin/gateway
COPY Rocket.toml .
EXPOSE 5001

CMD ["gateway"]