module.exports = async function (fastify, opts) {
  let userController = {
    create: async function (req, reply) {},
    get: async function (req, reply) {
      while (true) {}
    },
  };
  fastify.decorate("userController", userController);
};
