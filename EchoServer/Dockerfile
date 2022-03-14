FROM gradle:7.4-jdk17-alpine
ADD --chown=gradle . /code
WORKDIR /code
EXPOSE 5000
CMD ["gradle", "--stacktrace", "run"]