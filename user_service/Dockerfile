FROM rust:1.59 as builder

WORKDIR /usr/src/user_service
COPY . .

RUN cargo install --path .

FROM debian:buster-slim
RUN apt-get update && rm -rf /var/lib/apt/lists/*
COPY --from=builder ../usr/local/cargo/bin/user_service ../usr/local/bin/user_service
COPY Rocket.toml .
EXPOSE 5002

CMD ["user_service"]